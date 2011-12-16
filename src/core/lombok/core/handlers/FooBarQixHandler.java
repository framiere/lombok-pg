/*
 * Copyright © 2011 Philipp Eichhorn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lombok.core.handlers;

import static lombok.ast.AST.Arg;
import static lombok.ast.AST.Call;
import static lombok.ast.AST.Equal;
import static lombok.ast.AST.If;
import static lombok.ast.AST.LocalDecl;
import static lombok.ast.AST.MethodDecl;
import static lombok.ast.AST.Modulo;
import static lombok.ast.AST.Name;
import static lombok.ast.AST.New;
import static lombok.ast.AST.Return;
import static lombok.ast.AST.Type;
import static lombok.core.util.ErrorMessages.canBeUsedOnClassOnly;
import lombok.FooBarQix;
import lombok.RequiredArgsConstructor;
import lombok.ast.AST;
import lombok.ast.Expression;
import lombok.ast.IMethod;
import lombok.ast.IType;
import lombok.ast.Statement;
import lombok.core.DiagnosticsReceiver;

@RequiredArgsConstructor
public class FooBarQixHandler<TYPE_TYPE extends IType<? extends IMethod<TYPE_TYPE, ?, ?, ?>, ?, ?, ?, ?, ?>> {
	private static final String NUMBER_NAME = "number";
	private static final String VALUE_NAME = "value";
	private static final String RET_NAME = "ret";
	private static final String CONVERT_METHOD_NAME = "convert";

	private final TYPE_TYPE type;
	private final DiagnosticsReceiver diagnosticsReceiver;

	public void handle() {
		if (!type.isClass()) {
			diagnosticsReceiver.addError(canBeUsedOnClassOnly(FooBarQix.class));
			return;
		}

		generateGetPropertySupportMethod(type);
	}

	private void generateGetPropertySupportMethod(final TYPE_TYPE type) {
		type.injectMethod(MethodDecl(Type(String.class), CONVERT_METHOD_NAME) //
				.makePublic() //
				.withArgument(Arg(Type(Integer.class), NUMBER_NAME)) //
				.withStatement(LocalDecl(Type(String.class), VALUE_NAME).withInitialization( //
						Call(Name(Integer.class), "toString").withArgument(Name(NUMBER_NAME)))) //
				.withStatement(LocalDecl(Type(StringBuilder.class), RET_NAME).withInitialization( //
						New(Type(StringBuilder.class)))) //
				.withStatement(divisibleRule(3, "Foo")) //
				.withStatement(divisibleRule(5, "Bar")) //
				.withStatement(divisibleRule(7, "Qix")) //
				.withStatement(Return(Call(Name(RET_NAME), "toString"))));
	}

	private Statement<?> divisibleRule(int i, String value) {
		return If(Equal(Modulo(Name(NUMBER_NAME), AST.Number(i)), AST.Number(0))) //
				.Then(Call(Name(RET_NAME), "append").withArgument(AST.String(value))) //
				.Else(Call(Name(RET_NAME), "append").withArgument(AST.String("")));
	}

	/**
	 * 
	 * <pre>
	 * private static final String QIX = &quot;Qix&quot;;
	 * private static final String BAR = &quot;Bar&quot;;
	 * private static final String FOO = &quot;Foo&quot;;
	 * 
	 * &#064;Override
	 * public String convert(Integer number) {
	 * 	final String value = Integer.toString(number);
	 * 	final StringBuilder ret = new StringBuilder(value.length() * 3);
	 * 
	 * 	// divisibles rule
	 * 	ret.append(number % 3 == 0 ? FOO : &quot;&quot;);
	 * 	ret.append(number % 5 == 0 ? BAR : &quot;&quot;);
	 * 	ret.append(number % 7 == 0 ? QIX : &quot;&quot;);
	 * 
	 * 	// iterate on the digits and apply foo/bar/qix conversions
	 * 	final int length = value.length();
	 * 	for (int i = 0; i &lt; length; i++) {
	 * 		switch (value.charAt(i)) {
	 * 		case '3':
	 * 			ret.append(FOO);
	 * 			break;
	 * 		case '5':
	 * 			ret.append(BAR);
	 * 			break;
	 * 		case '7':
	 * 			ret.append(QIX);
	 * 			break;
	 * 		default:
	 * 			// no op rule
	 * 			break;
	 * 		}
	 * 	}
	 * 	return ret.length() == 0 ? value : ret.toString();
	 * }
	 * </pre>
	 */
}
