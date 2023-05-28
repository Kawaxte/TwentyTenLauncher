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

package ch.kawaxte.launcher;

import lombok.Getter;

/**
 * Enum representing supported languages within the application.
 *
 * <p>The constants in this Enum are based on the ISO 639-1 language codes. The field {@code
 * languageName} is the name of the language written in that language itself. For example, 'EN'
 * stands for English language and is represented as "English" in {@code languageName}.
 *
 * <p>The languages in this Enum are used in a {@link javax.swing.JComboBox} to display the list of
 * supported languages to the user.
 *
 * <p>The system property "user.language" is used to set a default language. The USER_LANGUAGE
 * variable holds this value.
 *
 * <h3>How to add new languages:</h3>
 *
 * <ol>
 *   <li>Add a new constant in this Enum that follows the ISO 639-1 language code.
 *   <li>Provide the localised name of the language as a string.
 *   <li>Add a new properties file to the resource bundle with the naming convention as follows:
 *       "messages_%languageCode%". Here, %languageCode% is the lowercase version of the Enum
 *       constant you just added.
 * </ol>
 *
 * @see <a href="https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">List of ISO 639-1 codes</a>
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
