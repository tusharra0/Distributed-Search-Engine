## Architecture

```mermaid
flowchart TD
    U[Client API] -->|POST /index| IC[Index Controller]
    U -->|GET /search?q=...| SC[Search Controller]

    IC --> NM[Node Manager]
    SC --> NM[Node Manager]

    subgraph Distribution Layer
        NM -->|assigns via consistent hashing| CH[Consistent Hashing]
        CH --> N1[Shard: Node A]
        CH --> N2[Shard: Node B]
    end

    subgraph Core Engine
        N1 --> II1[Inverted Index A]
        N2 --> II2[Inverted Index B]

        II1 --> QP1[Query Processor A]
        II2 --> QP2[Query Processor B]

        QP1 --> TF1[TF-IDF Calculator A]
        QP2 --> TF2[TF-IDF Calculator B]
    end

    %% Replication (future)
    N1 -. replicate .-> N2
    N2 -. replicate .-> N1
