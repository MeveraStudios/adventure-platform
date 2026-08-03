/*
 * This file is part of adventure-platform, licensed under the MIT License.
 *
 * Copyright (c) 2018-2020 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.adventure.platform.facet;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import net.kyori.adventure.text.object.SpriteObjectContents;
import net.kyori.adventure.text.renderer.ComponentRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Replaces object components with a plain text representation.
 *
 * <p>Object components (sprites and player heads) were added in Minecraft 1.21.9. Clients older
 * than that fail to decode a component that carries them, so they have to be replaced before the
 * message is serialized for such a client.</p>
 *
 * <p>The text representation matches what {@link net.kyori.adventure.text.flattener.ComponentFlattener#basic()}
 * produces, unless the object component declares a fallback, which is preferred when present.</p>
 *
 * @since 1.0.2
 */
public final class ObjectComponentDownsampler {
  private static final ComponentRenderer<Void> RENDERER = (component, context) -> downsample(component);
  private static final String UNKNOWN_PLAYER = "unknown player";

  private ObjectComponentDownsampler() {
  }

  /**
   * Replaces every object component within a component by its text representation.
   *
   * <p>The component is returned as-is when it does not contain any object component.</p>
   *
   * @param component a component
   * @return a component without object components
   * @since 1.0.2
   */
  public static @NotNull Component downsample(final @NotNull Component component) {
    Component result = component;

    switch (result) {
      case final ObjectComponent object -> {
        final Component fallback = object.fallback();
        result = Component.text()
          .append(fallback != null ? fallback : Component.text(describe(object.contents())))
          .style(object.style())
          .append(object.children())
          .build();
      }
      case final TranslatableComponent translatable -> result = downsampleArguments(translatable);
      case final SelectorComponent selector -> {
        final Component separator = downsampleSeparator(selector.separator());
        if (separator != null) result = selector.separator(separator);
      }
      case final NBTComponent<?> nbt -> {
        final Component separator = downsampleSeparator(nbt.separator());
        if (separator != null) result = nbt.separator(separator);
      }
      default -> {
      }
    }

    result = result.style(downsampleStyle(result.style()));

    final List<Component> children = result.children();
    List<Component> downsampledChildren = null;
    for (int i = 0; i < children.size(); i++) {
      final Component child = children.get(i);
      final Component downsampled = downsample(child);
      if (downsampled == child) continue;
      if (downsampledChildren == null) downsampledChildren = new ArrayList<>(children);
      downsampledChildren.set(i, downsampled);
    }

    return downsampledChildren == null ? result : result.children(downsampledChildren);
  }

  private static @NotNull Component downsampleArguments(final @NotNull TranslatableComponent component) {
    final List<TranslationArgument> arguments = component.arguments();
    List<TranslationArgument> downsampledArguments = null;

    for (int i = 0; i < arguments.size(); i++) {
      final TranslationArgument argument = arguments.get(i);
      if (!(argument.value() instanceof final Component value)) continue;
      final Component downsampled = downsample(value);
      if (downsampled == value) continue;
      if (downsampledArguments == null) downsampledArguments = new ArrayList<>(arguments);
      downsampledArguments.set(i, TranslationArgument.component(downsampled));
    }

    return downsampledArguments == null ? component : component.arguments(downsampledArguments);
  }

  private static @Nullable Component downsampleSeparator(final @Nullable Component separator) {
    if (separator == null) return null;
    final Component downsampled = downsample(separator);
    return downsampled == separator ? null : downsampled;
  }

  private static @NotNull Style downsampleStyle(final @NotNull Style style) {
    final HoverEvent<?> hover = style.hoverEvent();
    if (hover == null) return style;
    final HoverEvent<?> downsampled = hover.withRenderedValue(RENDERER, null);
    return downsampled == hover ? style : style.hoverEvent(downsampled);
  }

  private static @NotNull String describe(final @NotNull ObjectContents contents) {
    if (contents instanceof final SpriteObjectContents sprite) {
      final Key atlas = sprite.atlas();
      final String name = sprite.sprite().asMinimalString();
      return atlas.equals(SpriteObjectContents.DEFAULT_ATLAS) ? '[' + name + ']' : '[' + name + '@' + atlas.asMinimalString() + ']';
    } else if (contents instanceof final PlayerHeadObjectContents playerHead) {
      final String name = playerHead.name();
      return '[' + (name != null ? name : UNKNOWN_PLAYER) + " head]";
    }
    return "";
  }
}
