# Croquis's common libraries for Android

## Usage

## Requirements

## Installation
### 1. include repository
``` groovy
    repositories {
        maven {
            url 'https://github.com/croquiscom/Crary-Android/raw/master/deploy/releases'
        }
        maven {
            url 'https://github.com/croquiscom/Crary-Android/raw/master/deploy/snapshots'
        }
    }
```

### 2. dependency
#### snapshot
``` groovy
    compile 'com.croquis.crary:crary:0.1.0-SNAPSHOT@aar'
```
#### release
``` groovy
    compile 'com.croquis.crary:crary:0.1.0@aar'
```

## Build
```
    gradle clean build uploadArchives
```

## Author

* yamigo, yamigo1021@gmail.com
* sixmen, sixmen@gmail.com

## License

Crary is available under the MIT license. See the LICENSE file for more info.
