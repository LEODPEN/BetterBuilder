# BetterBuilder

[![GitHub license](https://img.shields.io/github/license/LEODPEN/BetterBuilder)](https://github.com/LEODPEN/BetterBuilder/blob/main/LICENSE) 
![Build status](https://img.shields.io/badge/build-passing-brightgreen)
[![Version](https://img.shields.io/badge/version-1.0.1-orange)](https://github.com/LEODPEN/BetterBuilder/releases)
---
BetterBuilder is a [Java annotation processor](https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/Processor.html) used for
automatically generating better builder codes([builder design pattern](https://en.wikipedia.org/wiki/Builder_pattern#Java)) with fluent get/set methods, 
which can make coding much more comfortable.

## Getting BetterBuilder

> BetterBuilder doesn't add any runtime dependencies to your code.

### Directly reach the jar

download from [releases](https://github.com/LEODPEN/betterBuilder/releases).

### Maven


## Usage

> Simple example; [See how to customization]{#customization}.
 
Given a class "Student":

```java
import cn.mpy634.annotation.BetterBuilder;
// All configurations are default.
@BetterBuilder
public class Student {
    private String name;
    private Integer ID;
}
```
The compiled code could be :
```java
public class Student {
    private String name;
    private Integer ID;
    public Student ID(Integer ID) {this.ID = ID;return this;}
    public Integer ID() {return this.ID;}
    public Student name(String name) {this.name = name;return this;}
    public String name() {return this.name;}
    public static Student.StudentBuilder builder() {return new Student.StudentBuilder();}
    public Student(String name, Integer ID) {this.name = name;this.ID = ID;}
    public Student() {}
    public static class StudentBuilder {
        private String name;
        private Integer ID;
        private StudentBuilder() {}
        public Student.StudentBuilder name(String name) {this.name = name;return this;}
        public Student.StudentBuilder ID(Integer ID) {this.ID = ID;return this;}
        public Student build() {return new Student(this.name, this.ID);}
    }
}
```

You can customize the generated code.

## Customization

### FluentSet switch

### FluentGet switch

### Set type

### NoBuilder switch

### Field ignore


## Todo list

- [x] fluent - builder / test

- [x] fluent - set / test

- [x] fluent - get / test

- [x] chain set options

- [ ] ignore set

- [ ] ignore get

- [ ] ignore build

- [x] compatible with lombok

...
