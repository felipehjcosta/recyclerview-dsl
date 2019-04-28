# RecyclerView DSL

[![CircleCI](https://circleci.com/gh/felipehjcosta/recyclerview-dsl.svg?style=svg)](https://circleci.com/gh/felipehjcosta/recyclerview-dsl)
[![codecov](https://codecov.io/gh/felipehjcosta/recyclerview-dsl/branch/master/graph/badge.svg)](https://codecov.io/gh/felipehjcosta/recyclerview-dsl)
[![codebeat badge](https://codebeat.co/badges/7e1861be-faec-4355-8f04-04abb53fdce0)](https://codebeat.co/projects/github-com-felipehjcosta-recyclerview-dsl-master)

A library for RecyclerView which aims to replace the adapter pattern by a DSL.

Usage
-----

```kotlin
val items = listOf(
        "Spider-Man",
        "Thor",
        "Iron Man",
        "Black Panther",
        "Black Widow",
        "Captain America",
        "Captain Marvel",
        "Falcon",
        "Hank Pym",
        "Hawkeye",
        "Hulk")

onRecyclerView(recyclerView) {
    withLinearLayout {
        orientation = LinearLayout.VERTICAL
        reverseLayout = false
    }

    bind(R.layout.strings_list_item) {
        withItems(items) {
            on<TextView>(R.id.title) {
                it.view?.text = it.item
            }

            onClick { position, item ->
                /* Handles item click */
            }
        }
    }
}
```

Download
--------

Gradle:
```groovy
compile 'com.github.felipehjcosta:recyclerview-dsl:0.7.0'
```
or Maven:
```xml
<dependency>
  <groupId>com.github.felipehjcosta</groupId>
  <artifactId>recyclerview-dsl</artifactId>
  <version>0.7.0</version>
  <type>pom</type>
</dependency>
```

License
-------

MIT License

Copyright (c) 2017 Felipe Costa

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
