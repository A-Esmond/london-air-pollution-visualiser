"""Trim the DEFRA PCM national CSVs down to the London map area.

The raw DEFRA files cover the whole UK (~255,000 rows each, ~160 MB in total),
but the app only ever shows the area inside the London map bounds. This script
keeps the 6-line header and every row whose easting/northing lies inside those
bounds, so the repo is small and the app starts quickly. Rows with missing
values are kept: filtering those out is the app's job (and is unit tested).

The bounds must match src/main/java/src/resources/LondonMapData.java.

Usage (from the repo root):
    python scripts/trim_to_london.py path/to/raw/UKAirPollutionData
"""
import csv
import sys
from pathlib import Path

MIN_EASTING, MAX_EASTING = 510394, 553297
MIN_NORTHING, MAX_NORTHING = 168504, 193305
HEADER_LINES = 6
OUTPUT_DIR = Path("src/main/resources/src/resources/UKAirPollutionData")


def in_london(row):
    try:
        x, y = int(row[1]), int(row[2])
    except (IndexError, ValueError):
        return False
    return MIN_EASTING <= x <= MAX_EASTING and MIN_NORTHING <= y <= MAX_NORTHING


def trim(source: Path, destination: Path):
    with source.open(newline="", encoding="utf-8") as f:
        lines = f.read().splitlines()
    header, body = lines[:HEADER_LINES], lines[HEADER_LINES:]
    kept = [line for line, row in zip(body, csv.reader(body)) if in_london(row)]
    destination.parent.mkdir(parents=True, exist_ok=True)
    destination.write_text("\n".join(header + kept) + "\n", encoding="utf-8")
    print(f"{source.name}: {len(body):,} rows -> {len(kept):,} rows")


def main():
    if len(sys.argv) != 2:
        sys.exit(__doc__)
    raw_dir = Path(sys.argv[1])
    for source in sorted(raw_dir.rglob("*.csv")):
        trim(source, OUTPUT_DIR / source.relative_to(raw_dir))


if __name__ == "__main__":
    main()
