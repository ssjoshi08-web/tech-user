#!/usr/bin/env bash
# =============================================================================
# Deploy script for user-api Spring Boot application.
# Usage:   ./scripts/deploy.sh
# Behavior: pull, build, stop existing app, deploy new jar, verify health,
#           and roll back automatically if the health check fails.
# =============================================================================
set -euo pipefail

# -----------------------------------------------------------------------------
# Configuration (override via environment variables)
# -----------------------------------------------------------------------------
APP_NAME="${APP_NAME:-user-api}"
APP_USER="${APP_USER:-$(whoami)}"
APP_HOME="${APP_HOME:-$HOME/apps/$APP_NAME}"
ARTIFACT_NAME="${ARTIFACT_NAME:-user-api.jar}"
SOURCE_JAR_PATH="${SOURCE_JAR_PATH:-target/$ARTIFACT_NAME}"
HEALTH_URL="${HEALTH_URL:-http://localhost:8080/actuator/health}"
HEALTH_TIMEOUT="${HEALTH_TIMEOUT:-60}"
SERVICE_NAME="${SERVICE_NAME:-${APP_NAME}.service}"
KEEP_BACKUPS="${KEEP_BACKUPS:-3}"
GIT_BRANCH="${GIT_BRANCH:-main}"

# -----------------------------------------------------------------------------
# Helpers
# -----------------------------------------------------------------------------
log()  { printf '\033[1;34m[deploy]\033[0m %s\n' "$*"; }
ok()   { printf '\033[1;32m[deploy]\033[0m %s\n' "$*"; }
warn() { printf '\033[1;33m[deploy]\033[0m %s\n' "$*" >&2; }
err()  { printf '\033[1;31m[deploy]\033[0m %s\n' "$*" >&2; }

require() {
    if ! command -v "$1" >/dev/null 2>&1; then
        err "Required command '$1' is not installed or not in PATH."
        exit 1
    fi
}

# -----------------------------------------------------------------------------
# Pre-flight checks
# -----------------------------------------------------------------------------
require git
require mvn
require java

if [ ! -d .git ]; then
    err "This script must be run from the root of the git repository."
    exit 1
fi

mkdir -p "$APP_HOME"
BACKUP_DIR="$APP_HOME/backups"
mkdir -p "$BACKUP_DIR"

# -----------------------------------------------------------------------------
# Step 1 — Pull latest code
# -----------------------------------------------------------------------------
log "Pulling latest code from branch '$GIT_BRANCH'..."
git fetch --all --prune
git checkout "$GIT_BRANCH"
git pull --ff-only origin "$GIT_BRANCH" || warn "Fast-forward pull failed — continuing with current HEAD"

# -----------------------------------------------------------------------------
# Step 2 — Build jar
# -----------------------------------------------------------------------------
log "Building application jar..."
mvn -B clean package -DskipTests
if [ ! -f "$SOURCE_JAR_PATH" ]; then
    err "Build artifact not found at $SOURCE_JAR_PATH"
    exit 1
fi
ok "Build complete: $SOURCE_JAR_PATH"

# -----------------------------------------------------------------------------
# Step 3 — Stop existing application
# -----------------------------------------------------------------------------
stop_application() {
    log "Stopping existing application '$APP_NAME'..."

    if command -v systemctl >/dev/null 2>&1 && systemctl list-unit-files "$SERVICE_NAME" >/dev/null 2>&1; then
        sudo -n systemctl stop "$SERVICE_NAME" || systemctl stop "$SERVICE_NAME" || true
    fi

    if pgrep -f "$ARTIFACT_NAME" >/dev/null 2>&1; then
        log "Killing running process..."
        pkill -f "$ARTIFACT_NAME" || true
        sleep 3
        pkill -9 -f "$ARTIFACT_NAME" || true
    fi
    ok "Application stopped."
}

stop_application

# -----------------------------------------------------------------------------
# Step 4 — Deploy new jar (with rotation of previous version)
# -----------------------------------------------------------------------------
TIMESTAMP="$(date +%Y%m%d%H%M%S)"

if [ -f "$APP_HOME/$ARTIFACT_NAME" ]; then
    log "Backing up previous jar -> $BACKUP_DIR/${ARTIFACT_NAME}.$TIMESTAMP"
    cp -f "$APP_HOME/$ARTIFACT_NAME" "$BACKUP_DIR/${ARTIFACT_NAME}.$TIMESTAMP"
    # Keep only the last N backups
    ls -1t "$BACKUP_DIR" | tail -n +$((KEEP_BACKUPS + 1)) | xargs -I {} rm -f "$BACKUP_DIR/{}"
fi

log "Deploying new jar to $APP_HOME/$ARTIFACT_NAME"
install -m 0644 "$SOURCE_JAR_PATH" "$APP_HOME/$ARTIFACT_NAME"

# -----------------------------------------------------------------------------
# Step 5 — Start the new jar
# -----------------------------------------------------------------------------
log "Starting application '$APP_NAME'..."
cd "$APP_HOME"
nohup java -jar "$ARTIFACT_NAME" > "$APP_HOME/app.log" 2>&1 &
NEW_PID=$!
disown $NEW_PID 2>/dev/null || true
log "Application launched with PID $NEW_PID"

# -----------------------------------------------------------------------------
# Step 6 — Verify health endpoint
# -----------------------------------------------------------------------------
log "Verifying health endpoint at $HEALTH_URL (timeout: ${HEALTH_TIMEOUT}s)..."

HEALTHY=false
for ((i=1; i<=HEALTH_TIMEOUT; i++)); do
    if curl -fsS "$HEALTH_URL" >/dev/null 2>&1; then
        HEALTHY=true
        break
    fi
    sleep 1
done

if [ "$HEALTHY" = "true" ]; then
    ok "Health check passed."
    curl -s "$HEALTH_URL" || true
    echo
    ok "Deployment of $APP_NAME completed successfully."
    exit 0
fi

# -----------------------------------------------------------------------------
# Rollback
# -----------------------------------------------------------------------------
err "Health check FAILED. Initiating rollback..."

LATEST_BACKUP="$(ls -1t "$BACKUP_DIR" 2>/dev/null | head -n 1 || true)"
if [ -z "$LATEST_BACKUP" ]; then
    err "No backup available — leaving the failed deployment in place."
    exit 1
fi

log "Rolling back to $LATEST_BACKUP"
stop_application
cp -f "$BACKUP_DIR/$LATEST_BACKUP" "$APP_HOME/$ARTIFACT_NAME"

nohup java -jar "$ARTIFACT_NAME" > "$APP_HOME/app.log" 2>&1 &
disown $! 2>/dev/null || true

sleep 5
if curl -fsS "$HEALTH_URL" >/dev/null 2>&1; then
    ok "Rollback successful — previous version is healthy."
    exit 0
fi

err "Rollback failed — manual intervention required."
exit 1
