# List of ports to kill
PORTS=(3000 3001 3002 3003)

# Kill any existing worker processes
for PORT in "${PORTS[@]}"; do
    PID=$(lsof -ti :"$PORT")  # Get PID of process using the port
    if [ -n "$PID" ]; then
        echo "Killing process on port $PORT (PID: $PID)"
        kill -9 "$PID"
    else
        echo "No process running on port $PORT"
    fi
done

javac Worker.java          # Compile Worker class
javac Router.java          # Compile Router class

# Start Worker processes on different ports
java -Dworker.port=3000 Worker &
java -Dworker.port=3001 Worker &
java -Dworker.port=3002 Worker &
java -Dworker.port=3003 Worker &

# Start Router process
java Router
