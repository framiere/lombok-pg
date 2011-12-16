package lombok;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.*;

@Target(TYPE)
@Retention(SOURCE)
public @interface FooBarQix {
}
