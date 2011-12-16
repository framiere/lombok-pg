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
import static lombok.ast.AST.Equal;
import static lombok.ast.AST.If;
import static lombok.ast.AST.MethodDecl;
import static lombok.ast.AST.Name;
import static lombok.ast.AST.Null;
import static lombok.ast.AST.Return;
import static lombok.ast.AST.String;
import static lombok.ast.AST.Type;
import static lombok.core.util.ErrorMessages.canBeUsedOnClassOnly;
import lombok.FooBarQix;
import lombok.RequiredArgsConstructor;
import lombok.ast.IMethod;
import lombok.ast.IType;
import lombok.core.DiagnosticsReceiver;

@RequiredArgsConstructor
public class FooBarQixHandler<TYPE_TYPE extends IType<? extends IMethod<TYPE_TYPE, ?, ?, ?>, ?, ?, ?, ?, ?>> {
	private static final String VALUE_NAME = "value";
	private static final String FOO_METHOD_NAME = "foobarqix";

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
		type.injectMethod(MethodDecl(Type(String.class), FOO_METHOD_NAME).makePublic().withArgument(Arg(Type(String.class), VALUE_NAME)) //
				.withStatement(If(Equal(Name(VALUE_NAME), Null())).Then(Block() //
						.withStatement(Return(String("nope")))) //
						.Else(Return(Name(VALUE_NAME)))));
	}
}
