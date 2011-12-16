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
import static lombok.ast.AST.Block;
import static lombok.ast.AST.Call;
import static lombok.ast.AST.Equal;
import static lombok.ast.AST.If;
import static lombok.ast.AST.LocalDecl;
import static lombok.ast.AST.MethodDecl;
import static lombok.ast.AST.Modulo;
import static lombok.ast.AST.Name;
import static lombok.ast.AST.New;
import static lombok.ast.AST.Number;
import static lombok.ast.AST.Return;
import static lombok.ast.AST.Type;
import static lombok.ast.AST.While;
import static lombok.core.util.ErrorMessages.canBeUsedOnClassOnly;
import lombok.FooBarQix;
import lombok.RequiredArgsConstructor;
import lombok.ast.AST;
import lombok.ast.Assignment;
import lombok.ast.Expression;
import lombok.ast.IMethod;
import lombok.ast.IType;
import lombok.ast.LocalDecl;
import lombok.ast.Statement;
import lombok.core.DiagnosticsReceiver;

@RequiredArgsConstructor
public class FooBarQixHandler<TYPE_TYPE extends IType<? extends IMethod<TYPE_TYPE, ?, ?, ?>, ?, ?, ?, ?, ?>> {
	private static final String NUMBER_NAME = "number";
	private static final String VALUE_NAME = "value";
	private static final String RET_NAME = "ret";
	private static final String LENGTH_NAME = "length";
	private static final String INDEX_NAME = "i";
	private static final String CHAR_NAME = "c";
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
				.withArgument(Arg(Type("int"), NUMBER_NAME)) //
				.withStatement(numberDeclaration()) //
				.withStatement(retDeclaration()) //
				.withStatement(lengthDeclaration()) //
				.withStatement(divisibleRule(3, "Foo")) //
				.withStatement(divisibleRule(5, "Bar")) //
				.withStatement(divisibleRule(7, "Qix")) //
				.withStatement(indexDeclaration()) //
				.withStatement(digitRules()) //
				.withStatement(returnRule()) //
		);
	}

	private LocalDecl retDeclaration() {
		return LocalDecl(Type(StringBuilder.class), RET_NAME).makeFinal().withInitialization( //
				New(Type(StringBuilder.class)));
	}

	private LocalDecl numberDeclaration() {
		return LocalDecl(Type(String.class), VALUE_NAME).makeFinal().withInitialization( //
				Call(Name(Integer.class), "toString").withArgument(Name(NUMBER_NAME)));
	}

	private LocalDecl lengthDeclaration() {
		return LocalDecl(Type("int"), LENGTH_NAME).withInitialization(Call(Name(VALUE_NAME), "length"));
	}

	private LocalDecl indexDeclaration() {
		return LocalDecl(Type("int"), INDEX_NAME).withInitialization(Number(0));
	}

	private Statement<?> divisibleRule(int i, String value) {
		return If(Equal(Modulo(Name(NUMBER_NAME), Number(i)), Number(0))) //
				.Then(Call(Name(RET_NAME), "append").withArgument(AST.String(value))); //
	}

	private LocalDecl charAtDeclaration() {
		return LocalDecl(Type("char"), CHAR_NAME).makeFinal().withInitialization( //
				Call(Name(VALUE_NAME), "charAt").withArgument(Name(INDEX_NAME)));
	}

	private Statement<?> digitRule(int i, String value) {
		return If(Equal(Name(CHAR_NAME), caracter(i))) //
				.Then(Call(Name(RET_NAME), "append").withArgument(AST.String(value))); //
	}

	private Expression<?> caracter(int i) {
		return AST.Number('0' + i);
//		return Call(Type(Character.class), "valueOf").withArgument(AST.String("" + i));
	}

	private Statement<?> digitRules() {
		return While(AST.Lower(Name(INDEX_NAME), Name(LENGTH_NAME))).Do( //
				Block().withStatement(charAtDeclaration()) //
						.withStatement(digitRule(3, "Foo")) //
						.withStatement(digitRule(5, "Bar")) //
						.withStatement(digitRule(7, "Qix")) //
						.withStatement(incrementIndex()));
	}

	private Assignment incrementIndex() {
		return AST.Assign(Name(INDEX_NAME), AST.Add(AST.Name(INDEX_NAME), AST.Number(1)));
	}

	private Statement<?> returnRule() {
		return If(Equal(Call(Name(RET_NAME), "length"), Number(0))) //
				.Then(Return(Name(VALUE_NAME))) //
				.Else(Return(Call(Name(RET_NAME), "toString")));
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
