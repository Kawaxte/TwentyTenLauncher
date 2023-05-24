# TwentyTen Launcher

[![GitHub Latest](https://img.shields.io/github/v/release/Kawaxte/TwentyTenLauncher?sort=date&logo=github&label=latest&style=for-the-badge)](https://github.com/Kawaxte/TwentyTenLauncher/releases/latest)
[![GitHub Pre-Release](https://img.shields.io/github/v/release/Kawaxte/TwentyTenLauncher?include_prereleases&sort=date&logo=github&label=pre-release&style=for-the-badge)](https://github.com/Kawaxte/TwentyTenLauncher/releases)

![GitHub Downloads](https://img.shields.io/github/downloads/Kawaxte/TwentyTenLauncher/total?sort=semver&logo=github&style=for-the-badge)

This repository contains an application that lets you play older versions of Minecraft. It's named
after the year the original Minecraft Launcher was released, and is
designed to give you an authentic experience of playing Minecraft as it was in the past.

## Features

- [x] Sign in with your Legacy, Mojang, or Microsoft account.
    - _Mojang accounts are not required to migrate to Microsoft accounts._
    - _Accounts that do not own a copy of Minecraft (Java Edition) will only be able to play
      without access to `online-mode=true` servers.
- [x] Play different versions of "Golden Age" Minecraft; from February 2010 up to
  September 2011.
- [x] Play Minecraft without an internet connection.
    - _In order to play the game without an internet connection, you must have played the game
      at least once while connected to the internet._
- [x] Hear the classic sounds of Minecraft, like the scary cave sounds and the infamous hurt sound.
- [x] Change the language of the launcher without having to restart the application.

---

## Contributing

[![GitHub Stars](https://img.shields.io/github/stars/Kawaxte/TwentyTenLauncher?logo=github&style=for-the-badge)](https://github.com/Kawaxte/TwentyTenLauncher/stargazers)
[![GitHub Contributors](https://img.shields.io/github/contributors/Kawaxte/TwentyTenLauncher?logo=github&style=for-the-badge)](https://github.com/Kawaxte/TwentyTenLauncher/graphs/contributors)

If you find this project useful or interesting, please consider giving it a ⭐. It's a
quick and easy way to show your support.

### Reporting Issues or Suggesting Features

[![GitHub Issues (Open)](https://img.shields.io/github/issues/Kawaxte/TwentyTenLauncher?logo=github&style=for-the-badge)](https://github.com/Kawaxte/TwentyTenLauncher/issues)
[![GitHub Issues (Closed)](https://img.shields.io/github/issues-closed/Kawaxte/TwentyTenLauncher?logo=github&style=for-the-badge)](https://github.com/Kawaxte/TwentyTenLauncher/issues?q=is%3Aissue+is%3Aclosed)

If you find a bug or have a suggestion for a new feature, you can report (or suggest) it by
following
these steps:

1. Go to the [issue tracker](https://github.com/Kawaxte/TwentyTenLauncher/issues) on this
   repository.
2. Click the `New issue` button.
3. Describe the issue or suggestion clearly and provide any relevant details.
4. Click the `Submit new issue` button to create the issue.

### Forking and Making Pull Requests

[![GitHub Pull Requests (Open)](https://img.shields.io/github/issues-pr/Kawaxte/TwentyTenLauncher?logo=github&style=for-the-badge)](https://github.com/Kawaxte/TwentyTenLauncher/pulls)
[![GitHub Pull Requests (Closed)](https://img.shields.io/github/issues-pr-closed/Kawaxte/TwentyTenLauncher?logo=github&style=for-the-badge)](https://github.com/Kawaxte/TwentyTenLauncher/pulls?q=is%3Apr+is%3Aclosed)

If you are a developer and would like to contribute by adding a new feature or
fixing a bug, you can do so by following these steps:

1. Make a copy of this repository by forking it on GitHub.
2. Make the changes you want in a new branch of your forked repository.
3. Test your changes thoroughly.
4. Submit a pull request from your forked repository with a detailed
   explanation of your changes.

---

## Building from Source

[![Java](https://img.shields.io/badge/Java-8%2B-blue?style=for-the-badge)](https://www.java.com/en/download/)
[![Maven](https://img.shields.io/badge/Maven-3.8.1-blue?logo=apachemaven&color=C71A36&style=for-the-badge)](https://maven.apache.org/download.cgi)
[![Git](https://img.shields.io/badge/Git-2.40.1-blue?logo=git&color=F05032&style=for-the-badge)](https://git-scm.com/downloads)

This project uses [Apache Maven](https://maven.apache.org/) to manage dependencies and build the
application. To build the application from source, follow these steps:

1. Clone the repository to your computer by using [Git](https://git-scm.com/) or by downloading the
   source code as a `.zip` file.
2. Load the project into your IDE of choice or open a terminal in the project directory.
3. Make any changes you want to the source code.
4. Run the command `mvn clean package shade:shade` to build the application or use the Run
   Configuration
   in your IDE.

The finished build will be saved as a `.jar` file in the `target` directory.

## License

[![GitHub License](https://img.shields.io/github/license/Kawaxte/TwentyTenLauncher?logo=github&style=for-the-badge)](https://github.com/Kawaxte/TwentyTenLauncher/blob/nightly/LICENSE)

This project is licensed under
the [GNU Lesser General Public License v3.0](https://choosealicense.com/licenses/lgpl-3.0/).