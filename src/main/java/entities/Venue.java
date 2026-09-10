package entities;

import java.time.ZoneId;
import java.util.UUID;

public record Venue (
    UUID id,
    String name,
    String address,
    ZoneId timezone
) {}