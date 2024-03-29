# Android Snowfall

[![](https://jitpack.io/v/leodan11/AndroidSnowfall.svg)](https://jitpack.io/#leodan11/AndroidSnowfall)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Implementation of Snowfall View on Android.


# Demo
![alt text](https://raw.githubusercontent.com/JetradarMobile/android-snowfall/master/art/hotellook-demo.gif)

# Credits

This is just an updated version of [android-snowfall](https://github.com/JetradarMobile/android-snowfall) and applying some of the active pull requests in it. 
Credits go completely to its creator and the people who have contributed with those pull requests.

# Installation

<details>
  <summary>Gradle</summary>

- Step 1. Add the JitPack repository to your build file

  Add it in your root build.gradle at the end of repositories:

  ```gradle
  allprojects {
    repositories {
      ...
      maven { url 'https://jitpack.io' }
      }
  }
  ```

- Step 2. Add the dependency
  
  ```gradle
  dependencies {
    implementation 'com.github.leodan11:AndroidSnowfall:{latest version}'
  }
  ```
  
</details>

<details>
    <summary>Kotlin</summary>

  - Step 1. Add the JitPack repository to your build file.

    Add it in your root build.gradle at the end of repositories:

    ```kotlin
    repositories {
        ...
        maven(url = "https://jitpack.io")
    }
    ```

- Step 2. Add the dependency
  
    ```kotlin
    dependencies {
      implementation("com.github.leodan11:AndroidSnowfall:${latest version}")
    }
    ```
  
</details>

# SnowfallView attributes

| Element  | Attribute  | Related methos(s)  | Default value  |
|---|---|---|---|
| Alpha max  | app:snowflakeAlphaMax  |   | 250  |
| Alpha min | app:snowflakeAlphaMin  |   | 150  |
| Angle max | app:snowflakeAngleMax  |   | 10  |
| Image | app:snowflakeImage  | `setSnowflakeImageDrawable`<br/>`setSnowflakeImageBitmap`<br/>`setSnowflakeResource`  | `null`  |
| Size max | app:snowflakeSizeMax  |   | 8dp  |
| Size min | app:snowflakeSizeMin  |   | 2dp  |
| Speed max | app:snowflakeSpeedMax  |   | 8  |
| Speed min | app:snowflakeSpeedMin  |   | 2  |
| Already falling | app:snowflakesAlreadyFalling  |   | false  |
| Fading enabled | app:snowflakesFadingEnabled  |   | false  |
| Multiple Images | app:snowflakesMultipleImages  |   | false  |
| Number of elements | app:snowflakesNum  |   | 200  |

Alternatively, you can also style the Snowfall View programmatically by calling the methods:

 It's also possible to add the image of type drawable using the following method:
- `setSnowflakeImageDrawable(Drawable)`

 It's also possible to add the image of type bitmap using the following method:
- `setSnowflakeImageBitmap(Bitmap)`

 It's also possible to add the image of type resource, e.g. `R.drawable.snowflake` using the following method:
- `setSnowflakeResource(Resource)`

To add multiple images, use the following methods, following the rule of the above methods:
- `setSnowflakeImageDrawables(List<Drawable>)`
- `setSnowflakeImageBitmaps(List<Bitmap>)`
- `setSnowflakeResources(List<Resource>)`


Usage
-----

Default implementation with round snowflakes:

```xml
<com.leodan11.snowfall.SnowfallView
      android:layout_width="match_parent"
      android:layout_height="match_parent" />

```

Fully customized implementation:

```xml
<com.leodan11.snowfall.SnowfallView
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      app:snowflakesNum="250"
      app:snowflakeAlphaMin="150"
      app:snowflakeAlphaMax="255"
      app:snowflakeAngleMax="5"
      app:snowflakeSizeMin="8dp"
      app:snowflakeSizeMax="32dp"
      app:snowflakeSpeedMin="4"
      app:snowflakeSpeedMax="12"
      app:snowflakesFadingEnabled="true"
      app:snowflakesAlreadyFalling="false"
      app:snowflakesMultipleImages="false"
      app:snowflakeImage="@drawable/snowflake"/>

```
