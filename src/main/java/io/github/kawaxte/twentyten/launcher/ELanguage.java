/*
 * Copyright (C) 2023 Kawaxte
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.kawaxte.twentyten.launcher;

import lombok.Getter;

/**
 * This enum represents the various languages that are supported by the launcher. Each constant of
 * this enum represents a language according to ISO 639-1 format.
 *
 * <p>Each language is represented by two parts:
 *
 * <ol>
 *   <li>the constant name (e.g., EN for English), and
 *   <li>the language's name in its own language (e.g., "English" for EN).
 * </ol>
 *
 * This structure is designed to support the display of the language's name in a {@link
 * javax.swing.JComboBox}, allowing users to select their preferred language based on its name
 * rather than its ISO 639-1 code.
 *
 * <p>In addition to these constants, this enum also provides the {@code USER_LANGUAGE} field, which
 * captures the system property for the user's current language.
 *
 * <p>To add a new language to the launcher:
 *
 * <ol>
 *   <li>Add a new constant here using the ISO 639-1 format.
 *   <li>Provide the name of the language as it is in the new language itself.
 * </ol>
 *
 * @author Kawaxte
 * @since 1.5.0923_03
 */
public enum ELanguage {
  BG("Български"),
  CS("Čeština"),
  DE("Deutsch"),
  EN("English"),
  ES("Español"),
  ET("Eesti"),
  FI("Suomi"),
  FR("Français"),
  HU("Magyar"),
  IT("Italiano"),
  JA("日本語"),
  PL("Polski"),
  RU("Русский");

  public static final String USER_LANGUAGE;

  static {
    USER_LANGUAGE = System.getProperty("user.language");
  }

  @Getter private final String languageName;

  ELanguage(String languageName) {
    this.languageName = languageName;
  }
}
