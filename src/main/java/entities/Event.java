package entities;

import java.time.ZonedDateTime;
import java.util.UUID;

import enums.EventStatus;
import rules.PricingRules;

public record Event (
    UUID id,
    UUID venueId,
    String title,
    ZonedDateTime start,
    ZonedDateTime end,
    EventStatus status,
    PricingRules pricingRules
) {}