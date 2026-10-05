# entities-vsk-database-roundtable

Side-by-side benchmark rounds that run one cloud-serving workload against several databases in CI.

## What it is for

Each database has its own workflow, which starts that database, runs the `ycsb` read-modify-write workload against it, and uploads the output. A report script turns the downloaded output into throughput and latency charts, so the databases compare on one workload.

## Building and running

Each round runs as a workflow on push. The report script runs from a directory holding the downloaded results.

## Licence

MIT. See [LICENSE](LICENSE).
