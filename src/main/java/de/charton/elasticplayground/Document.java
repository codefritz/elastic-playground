package de.charton.elasticplayground;

import lombok.Builder;

@Builder
public record Document(String id, String title, String description) {
}
