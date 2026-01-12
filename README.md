# Epidemic Simulator

Agent-based epidemic simulation engine. Agents move on a hex grid, infect neighbors based on distance-weighted probabilities, and recover over time.

## Quick Start

**Full stack run**
```bash
# terminal 1: Backend
sbt "runMain simulator.SimulatorServer"

# terminal 2: Frontend
cd src/main/frontend && npm install && npm run dev
```

**Headless CLI:**
```bash
sbt "runMain simulator.Main"
```

## Configuration

Edit [application.conf](src/main/resources/application.conf):

## Architecture

- **Board** — Hex grid with preprocessed neighbor layers for fast distance queries
- **Person** — Agent with hex movement patterns and infection state
- **Disease** — Trait defining transmission rules (distance decay, recovery time, mortality)
- **InfectionMap** — Spatial infection probability calculator using independent event composition
- **SimulatorServer** — Akka HTTP WebSocket server streaming simulation state to frontend

## Extensibility
- Add new diseases by implementing the `Disease` trait
- Customize movement patterns in the `Person` class
- Modify infection logic in `InfectionMap` for different transmission models