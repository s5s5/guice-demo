package WithGuice1_Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
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

final class Owner {
  private final Car car;

  // @Inject 有两个作用：
  // 1. 用于构造函数，表示这个构造函数是被Guice管理的，Guice会自动调用这个构造函数
  // 2. 用于成员变量，表示这个成员变量是被Guice管理的，Guice会自动注入这个成员变量
  @Inject
  Owner(Car car) {
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
    //bind(the interface/superclass).to(implementation/subclass)
    bind(Car.class).to(BMW.class).in(Singleton.class); // Singleton.class 保证只创建一个实例

    //Method 2
    //bind the class to Instance (singleton) 保证只创建一个实例
    //    bind(Car.class).toInstance(BMW.create());
  }

  //Method 3
  //Use Provides Annotation
  // @Provides 与 @Inject 区别：
  // @Inject 用于构造函数和成员变量，表示这个构造函数或成员变量是被 Guice 管理的，Guice 会自动调用这个构造函数或注入这个成员变量
  // @Provides 用于方法，表示这个方法是被 Guice 管理的，Guice 会自动调用这个方法
  //  @Provides
  //  @Singleton
  //  // @Singleton 保证每次调用这个方法时，返回的都是同一个实例
  //  Car provideCar() {
  //    return BMW.create();
  //  }
}

public class SingletonExample {
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
