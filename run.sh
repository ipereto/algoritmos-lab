#!/usr/bin/env bash
# Compila con javac (sin dependencias) y ejecuta el lab pedido.
#   ./run.sh          → menú
#   ./run.sh 02       → un lab
#   ./run.sh all      → labs 00 a 08
#   ./run.sh retos    → tus retos (lab 09); ./run.sh retos 4 → solo el reto 4
set -euo pipefail
cd "$(dirname "$0")"
rm -rf out
mkdir -p out
javac --release 21 -encoding UTF-8 -d out $(find src/main/java -name '*.java')
exec java -Xss16m -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp out lab.Main "$@"
