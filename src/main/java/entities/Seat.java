package entities;

import java.util.Map;
import java.util.UUID;

public record Seat (
    UUID id,
    UUID venueId,
    String section,
    String row ,
    int number,
    Map<String, Object> attributes
) {}