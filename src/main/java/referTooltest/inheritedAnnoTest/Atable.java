package referTooltest.inheritedAnnoTest;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Atable {
    public String name() default "";
}
