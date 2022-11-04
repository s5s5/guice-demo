# 依赖注入DI -- Guice

视频教程：https://www.bilibili.com/video/BV1qr4y137qW

## 理论

- 依赖注入
    - 是一种消除类之间依赖关系的设计模式。例如，A 类要依赖 B 类，A 类不再直接创建 B 类，而把这种依赖关系由 Guice （用 abstractModule）或 Spring 容器（xml, bean）根据配置信息来创建、管理
- 为什么需要使用依赖注入
    - 比如我们有一个 car class，它有各种对象，比如车轮
    - car class 需要**创建**所有依赖的对象，但我们想要换个品牌的车轮
        - 使用依赖注入，不需要创建，只要使用即可
        - 依赖注入是代码的中间人，他创建车轮对象，并提供能 car class 使用
- 控制反转 ⇒ 依赖注入背后的概念
    - 指一个类不应静态配置其依赖项，而应由其他一些类从外部进行配置
        - 这是 S.O.L.I.D 的第五项原则：类应该依赖于抽象，而不是依赖于具体的东西
    - 根据这些原则，一个类应该专注于履行其职责，而不是创建履行这些职责的的对象。这就是依赖注入发挥作用的地方，它为类提供了必需的对象。
- 优势
    - 帮助单测
    - 依赖关系的初始化由注入器完成，减少了代码
    - 扩展程序更加容易
    - 松耦合
- 劣势
    - 学习有点复杂，过度使用会导致管理和其它问题
    - 编译时的错误被推送到运行时
    - 影响 IDE 自动化

## 安装

Guice 在 5.x 版本后整合了低版本的扩展类库，目前使用其所有功能只需要引入一个依赖

```xml
<dependency>
    <groupId>com.google.inject</groupId>
    <artifactId>guice</artifactId>
    <version>5.1.0</version>
</dependency>
```

## 关键词

`@Inject` 有两个作用：
1. 用于构造函数，表示这个构造函数是被Guice管理的，Guice会自动调用这个构造函数
2. 用于成员变量，表示这个成员变量是被Guice管理的，Guice会自动注入这个成员变量

```
@Inject
BMW() {}
```

- `AbstractModule` 是 Guice 提供的抽象类，用于创建 Guice Module
- Guice Module 用于告诉 Guice 如何创建一个类的实例

```java
final class CarModule extends AbstractModule {
  // configure 方法用于配置 Guice Module
  @Override
  protected void configure() {
    // bind 方法用于绑定一个类到一个实现类
    bind(Car.class).to(BMW.class);
  }
}
```

- `Guice.createInjector` 方法用于创建一个 Guice Injector
- 传入一个 `CarModule` 对象，让 Guice 把 `Car` 绑定到 `BMW` 实现类

```java
Injector injector = Guice.createInjector(new CarModule());
// injector.getInstance 从 Guice 中获取一个实例
Owner owner = injector.getInstance(Owner.class);
```

- bind 方法用于绑定一个类到一个实现类 （常用）

```java
bind(Car.class).to(BMW.class);
```

`@Provides` 与 `@Inject` 区别：
- `@Inject` 用于构造函数和成员变量，表示这个构造函数或成员变量是被 Guice 管理的，Guice 会自动调用这个构造函数或注入这个成员变量
- `@Provides` 用于方法，表示这个方法是被 Guice 管理的，Guice 会自动调用这个方法 （常用）

```java
@Provides
Car provideCar() {
  return BMW.create();
}
```

`@Singleton` 是 Guice 提供的注解，用于标记一个类为单例类
- 单例类的实例只会被创建一次，之后每次调用都会返回同一个实例
- 好处是可以减少内存开销，但是会导致类的状态不可控

```java
// 标记一个实例的三种方法:

// 方法一 （常用）
bind(Car.class).to(BMW.class).in(Singleton.class); // Singleton.class 保证只创建一个实例
// 方法二
bind(Car.class).toInstance(BMW.create());
// 方法三 （常用）
@Provides
@Singleton
Car provideCar() {
  return BMW.create();
}
```


`Provider` 是 Guice 提供的接口，用于延迟创建对象

```java
@Inject
Owner(Provider<Car> carProvider) {
  // 懒汉模式获取对象
  this.car = carProvider.get();
}
```

`@Qualifier` 是 Guice 提供的注解，用于标记一个类的实现类
- 它用于解决一个接口有多个实现类的情况
- `@Named` 是 Guice 以前的版本提供的注解，现在已经被 `@Qualifier` 替代

```java
// 使用指定的实现类 CarBMW
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@interface CarBMW {}
```
```java
@Inject
Owner(@CarBMW Car car) { // 通过 @CarBMW 来标记
  this.car = car;
}
```
```java
// Provides 的时候声明 annotation 为 CarBMW （常用）
@Provides
@CarBMW
Car provideBMW() {
  return BMW.create();
}
```
```java
// 在 bind 的时候声明 annotation 为 CarBMW （用得少）
bind(Car.class).annotatedWith(CarBMW.class).to(BMW.class);
```

