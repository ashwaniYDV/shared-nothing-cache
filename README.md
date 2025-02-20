# In-memory Cache based on shared-nothing architecture at process/core level
### About
* 4 separate worker processes running in 4 different cores, each with an in-memory cache (HashMap).
* A router process that distributes requests using modulo hashing.
* Inter-process communication (IPC) between the router process and the worker processes.

<img width="639" alt="image" src="https://github.com/user-attachments/assets/ddda006b-4c8c-4e3f-a157-7f9cdd44dbc5" />

### Shared-Nothing Architecture (SN Architecture)
* Each worker process operates independently with its own in-memory cache.
* There is no shared state between processes.
* Communication happens through message passing via Sockets.

### Process-Level Sharding
* Data is partitioned across processes based on modulo hashing.
* Each worker handles a portion of requests determined by the router.

### Actor Model (in a Loose Sense)
* Each worker acts like an actor, processing its own messages independently.
* No direct shared memory; workers communicate via messages (Sockets).

### Embarrassingly Parallel Architecture (for Cache Operations)
* Since each worker only works on its own cache, there is no interdependency between workers.

### How to run
```
chmod +x ./runner.sh
./runner.sh  
```
