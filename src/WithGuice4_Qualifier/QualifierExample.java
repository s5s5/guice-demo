package WithGuice4_Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;

interface Car {
  int drive();
}

// @Singleton 是 Guice 提供的注解，用于标记一个类为单例类
// 单例类的实例只会被创建一次，之后每次调用都会返回同一个实例
@Singleton
final class BMW implements Car {
  // @Inject 有两个作用：
  // 1. 用于构造函数，表示这个构造函数是被Guice管理的，Guice会自动调用这个构造函数
  // 2. 用于成员变量，表示这个成员变量是被Guice管理的，Guice会自动注入这个成员变量
  @Inject
  BMW() {}

  @Override
  public int drive() {
    return 60;
  }

  public static BMW create() {
    // 比如是第三方库提供的类，没有使用 Guice，那么就需要手动创建实例
    return new BMW();
  }
}

@Singleton
final class Benz implements Car {
  // @Inject 有两个作用：
  // 1. 用于构造函数，表示这个构造函数是被Guice管理的，Guice会自动调用这个构造函数
  // 2. 用于成员变量，表示这个成员变量是被Guice管理的，Guice会自动注入这个成员变量
  @Inject
  Benz() {}

  @Override
  public int drive() {
    return 80;
  }

  public static Benz create() {
    // 比如是第三方库提供的类，没有使用 Guice，那么就需要手动创建实例
    return new Benz();
  }
}

final class Owner {
  private final Car car;

  // @Inject 有两个作用：
  // 1. 用于构造函数，表示这个构造函数是被Guice管理的，Guice会自动调用这个构造函数
  // 2. 用于成员变量，表示这个成员变量是被Guice管理的，Guice会自动注入这个成员变量
  @Inject
  Owner(@CarBenz Car car) {
    // Provider 是 Guice 提供的接口，用于延迟创建对象
    // 通过 Provider.get() 方法可以获取对象
    this.car = car;
  }

  public Car getCar() {
    return this.car;
  }
}

// AbstractModule 是 Guice 提供的抽象类，用于创建 Guice Module
// Guice Module 用于告诉 Guice 如何创建一个类的实例
final class CarModule extends AbstractModule {
  // configure 方法用于配置 Guice Module
  // 在这个方法中，我们告诉 Guice，当需要一个 Car 类型的实例时，应该创建一个 BMW 类型的实例
  @Override
  protected void configure() {
    //Method 1
    //    bind(Car.class).annotatedWith(CarBMW.class).to(BMW.class);
    //    bind(Car.class).annotatedWith(CarBenz.class).to(Benz.class);
  }

  //Method 2
  @Provides
  @CarBMW
  Car provideBMW() {
    return BMW.create();
  }

  @Provides
  @CarBenz
  Car provideBenz() {
    return Benz.create();
  }
}

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@interface CarBMW {}

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@interface CarBenz {}

public class QualifierExample {
  public static void main(String[] args) {
    // Guice.createInjector 方法用于创建一个 Guice Injector
    // Guice Injector 用于创建 Guice 管理的类的实例
    // 在这个方法中，我们传入一个 CarModule 对象，告诉 Guice 我们需要一个 Car 类型的实例
    // Guice 会根据 CarModule 中的配置，创建一个 BMW 类型的实例
    Injector injector = Guice.createInjector(new CarModule());

    // Guice Injector 用于创建 Guice 管理的类的实例
    // 在这个方法中，我们传入一个 Owner 类型的对象，告诉 Guice 我们需要一个 Owner 类型的实例
    // Guice 会根据 Owner 类中的 @Inject 注解，自动创建一个 Owner 类型的实例
    Owner owner = injector.getInstance(Owner.class);

    System.out.println(
        "With Guice car example: This car can drive at speed: " + owner.getCar().drive());
  }
}
