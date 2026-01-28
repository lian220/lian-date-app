#!/bin/bash

# Install Git Hooks Script
# Copies pre-commit hook from scripts/git-hooks to .git/hooks

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
GIT_HOOKS_DIR="$PROJECT_ROOT/.git/hooks"
SCRIPTS_HOOKS_DIR="$PROJECT_ROOT/scripts/git-hooks"

echo "📦 Installing Git Hooks..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Check if .git/hooks directory exists
if [ ! -d "$GIT_HOOKS_DIR" ]; then
    echo "❌ Error: .git/hooks directory not found!"
    echo "   Make sure you're in a Git repository"
    exit 1
fi

# Copy pre-commit hook
if [ -f "$SCRIPTS_HOOKS_DIR/pre-commit" ]; then
    cp "$SCRIPTS_HOOKS_DIR/pre-commit" "$GIT_HOOKS_DIR/pre-commit"
    chmod +x "$GIT_HOOKS_DIR/pre-commit"
    echo "✅ Pre-commit hook installed successfully"
else
    echo "❌ Error: scripts/git-hooks/pre-commit not found!"
    exit 1
fi

echo ""
echo "🎉 All hooks installed!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📝 How to use:"
echo "   - Hooks will automatically run on 'git commit'"
echo "   - If tests fail, commit will be blocked"
echo "   - Fix the issues and try committing again"
echo ""
