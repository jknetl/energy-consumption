# Energy Consumption — Bruno Collection

This directory contains a [Bruno](https://www.usebruno.com/) API collection for manually exploring and testing the Energy Consumption REST API.

## Structure

```
bruno-collection/
├── environments/
│   └── local.yml       # Environment with backendBaseUrl pointing at localhost:8080
├── Locations/          # Location CRUD requests
├── Meters/             # Meter CRUD requests
└── Readings/           # Meter reading requests
```

## Usage

1. Install [Bruno](https://www.usebruno.com/) (desktop app or CLI).
2. Open the collection: **File → Open Collection** → select the `bruno-collection/` folder.
3. Select the **local** environment.
4. Make sure the application is running locally (see the root README for setup instructions).
5. Run requests individually or as a collection.

## Environments

| Environment | Base URL |
|---|---|
| local | `http://localhost:8080/api` |
