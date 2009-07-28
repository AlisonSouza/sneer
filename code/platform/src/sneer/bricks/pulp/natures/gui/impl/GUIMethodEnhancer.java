package sneer.bricks.pulp.natures.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.ram.collections.Collections;
import sneer.bricks.pulp.natures.gui.GUINatureRuntime;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.lang.Functor;
import sneer.foundation.lang.Pair;

public class GUIMethodEnhancer {

	private final String _thunkName;
	private final List<ClassDefinition> _resultingClasses;
	private final CtMethod _method;
	private final ClassPool _classPool;
	private final CtClass _containingClass;

	public GUIMethodEnhancer(ClassPool classPool, CtClass containingClass, CtMethod method, List<ClassDefinition> resultingClasses) {
		_classPool = classPool;
		_containingClass = containingClass;
		_resultingClasses = resultingClasses;
		_method = method;
		_thunkName = "_" + method.getName();
	}

	public void run() {
		try {
			createDelegate();
			_resultingClasses.add(makeThunkFor());
			enhanceMethod();
		} catch (Exception e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(_method.toString(), e);
		}
	}

	private void createDelegate() throws CannotCompileException {
		_containingClass.addMethod(CtNewMethod.copy(_method, delegateMethodName(), _method.getDeclaringClass(), null));
	}

	private String delegateMethodName() {
		return "$" + _method.getName();
	}
	
	private void enhanceMethod() throws CannotCompileException, NotFoundException {
		final String thunkFullName = _method.getDeclaringClass().getName() + "." + _thunkName;
		_method.setBody(
					"{ " + thunkFullName + " thunk = new " + thunkFullName + "(" + thunkConstructorArguments() + ");"
					+ GUINatureRuntime.class.getName() + ".runInGuiThread(natures.gui.BrickMetadata.ENVIRONMENT, thunk);"
					+ (hasReturnValue() ? "return thunk.result; " : "return;") + " }");
	}

	private String thunkConstructorArguments() throws NotFoundException {
		StringBuilder parameterList = new StringBuilder("this");
		for (int i=0; i<_method.getParameterTypes().length; ++i) {
			parameterList.append(", ");
			parameterList.append("$" + (i + 1));
		}
		return parameterList.toString();
	}

	private boolean hasReturnValue() {
		return !_method.getSignature().endsWith(")V");
	}

	private ClassDefinition makeThunkFor() throws NotFoundException,
			CannotCompileException, IOException {
		final CtClass thunkClass = _containingClass.makeNestedClass(_thunkName, true);
		thunkClass.addInterface(ctClassFor(Runnable.class));
		
		final ArrayList<Pair<String, String>> thunkFields = buildThunkFieldList();
		defineThunkFields(thunkClass, thunkFields);
		
		final String ctorCode = "public " + _thunkName + "(" + thunkParameterList(thunkFields) + ") {\n" +
			thunkFieldAssignments(thunkFields) + 
		"}";
		thunkClass.addConstructor(
				CtNewConstructor.make(
					ctorCode, thunkClass));
		
		final String invocation = "target." + delegateMethodName() + "(" + targetInvocationList(thunkFields) + ");";
		thunkClass.addMethod(
				CtNewMethod.make(
					"public void run() {" +
						(hasReturnValue() ? "result = " + invocation : invocation)+
					"}", thunkClass));
		
		return new ClassDefinition(thunkClass.getName(), thunkClass.toBytecode());
	}

	private void defineThunkFields(final CtClass thunkClass,
			final ArrayList<Pair<String, String>> thunkFields)
			throws CannotCompileException, NotFoundException {
		for (Pair<String, String> thunkField : thunkFields)
			thunkClass.addField(CtField.make("private final " + thunkField.a + " " + thunkField.b + ";", thunkClass));
		
		if (hasReturnValue())
			thunkClass.addField(CtField.make("public " + _method.getReturnType().getName() + " result;", thunkClass));
	}

	private ArrayList<Pair<String, String>> buildThunkFieldList()
			throws NotFoundException {
		ArrayList<Pair<String, String>> thunkFields = new ArrayList<Pair<String, String>>();
		thunkFields.add(Pair.of(_method.getDeclaringClass().getName(), "target"));
		final CtClass[] parameters = _method.getParameterTypes();
		for (int i = 0; i < parameters.length; i++) {
			thunkFields.add(Pair.of(parameters[i].getName(), "arg" + i));
		}
		return thunkFields;
	}

	private String targetInvocationList(ArrayList<Pair<String, String>> thunkFields) {
		return my(Lang.class).strings().join(
			my(Collections.class).map(
					thunkFields.subList(1, thunkFields.size()),	Pair.<String, String>second()),	", ");
	}	
	
	private String thunkFieldAssignments(ArrayList<Pair<String, String>> thunkFields) {
		return my(Lang.class).strings().join(
			my(Collections.class).map(
			thunkFields, new Functor<Pair<String, String>, String>() { @Override public String evaluate(Pair<String, String> input) {
				return "this." + input.b + " = " + input.b + ";";
			}}), "\n");
	}

	private String thunkParameterList(ArrayList<Pair<String, String>> thunkFields) {
		return my(Lang.class).strings().join(
			my(Collections.class).map(
			thunkFields, new Functor<Pair<String, String>, String>() { @Override public String evaluate(Pair<String, String> input) {
					return input.a + " " + input.b;
			}}), ", ");
	}

	private CtClass ctClassFor(final Class<?> clazz) throws NotFoundException {
		return _classPool.get(clazz.getName());
	}
}