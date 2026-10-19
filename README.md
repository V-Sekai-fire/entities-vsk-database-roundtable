# entities-vsk-database-roundtable

Side-by-side benchmark rounds that run one cloud-serving workload against several databases in CI.

## What it is for

Each database has its own workflow, which starts that database, runs the `ycsb` read-modify-write workload against it, and uploads the output. A report script turns the downloaded output into throughput and latency charts, so the databases compare on one workload.

`sqlite-fdb-game-day` is the one round shaped like a game instead. Sixteen players save at 20 Hz for one player-day (1,728,000 saves) beside bulk writes, and it reports the slowest saves a day reaches, not the 99th percentile. It runs FoundationDB alone under the same players first, so the store's share of the wait shows. Two legs run side by side: the store as pinned, and the store with one fold in flight. The first leg also runs a small frontier search from the store's seed levels.

## Building and running

Each round runs as a workflow on push. The report script runs from a directory holding the downloaded results.

## Licence

MIT. See [LICENSE](LICENSE).
