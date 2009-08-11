package sneer.tests.freedom7;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import sneer.tests.SovereignFunctionalTestBase;
import sneer.tests.adapters.LoggerForTests;

public abstract class Freedom7TestBase extends SovereignFunctionalTestBase {
	
//	private final JavaCompiler _compiler = my(JavaCompiler.class);

	@Ignore
	@Test (timeout = 10000)
	public void meToo() throws Exception {
		LoggerForTests.isOn = true;
		
		a().copyToSourceFolder(generateY());
		
		newSession(a());
		
		a().waitForAvailableBrick("freedom7.y.Y");
		
		b().waitForAvailableBrick("freedom7.y.Y");
		b().stageBrickForExecution("freedom7.y.Y");

		System.clearProperty("freedom7.y.Y.installed");
		newSession(b());
		assertEquals("true", System.getProperty("freedom7.y.Y.installed"));
	}

	@Test
	@Ignore
	public void testPublishBrickWithTupleType() throws Exception {
//		System.clearProperty("freedom7.v.V.installed");
//		publisher().installBricks(generateV());
//		assertEquals("true", System.getProperty("freedom7.v.V.installed"));
	}
	
	@Test
	@Ignore
	public void testPublishTwoBricks() throws Exception {
//		System.clearProperty("freedom7.y.Y.installed");
//		System.clearProperty("freedom7.w.W.installed");
//		publisher().installBricks(generateWandY());
//		assertEquals("true", System.getProperty("freedom7.y.Y.installed"));
//		assertEquals("true", System.getProperty("freedom7.w.W.installed"));
	}
	
	@Test
	@Ignore
	public void testIndirectDependencies() {
		fail("indirect dependencies break compareTo");
	}
	
	@Test
	@Ignore
	public void testIndirectCycles() {
		fail("indirect cycles, what's the doubt?");
	}

	@Test
	@Ignore
	public void testPublishBrickWithDependencies() throws Exception {
//		System.clearProperty("freedom7.w.W.installed");
//		publisher().installBricks(generateY());
//		publisher().installBricks(generateW());
//		assertEquals("true", System.getProperty("freedom7.w.W.installed"));
	}
	
	@Test
	@Ignore
	public void testPublishBrickWithLib() throws Exception {
//		System.clearProperty("freedom7.lib.Lib.executed");
//		publisher().installBricks(generateX());
//		assertEquals("true", System.getProperty("freedom7.lib.Lib.executed"));
	}
		
	@Ignore
	@Test
	public void testMeTooSingleBrick() throws Exception {
	}
	
//	private File generateX() throws IOException {
//		generateLib(sourceFolder("x/freedom7/x/impl/lib/lib.jar"));
//		
//		final File src = sourceFolder("x");
//		final SourceFileWriter writer = new SourceFileWriter(src);
//		writer.write("freedom7.x.X",
//				"public interface X extends sneer.brickness.Brick {" +
//				"}");
//		writer.write("freedom7.x.impl.XImpl",
//				"class XImpl implements freedom7.x.X {\n" +
//					"public XImpl() {\n" +
//						"freedom7.lib.Lib.execute();\n" +
//					"}" +
//				"}");	
//		return src;
//	}

//	private void generateLib(final File libJar) throws IOException {
//		final File lib = sourceFolder("lib");
//		new SourceFileWriter(lib).write("freedom7.lib.Lib",
//			"public class Lib {" +
//				"public static void execute() {" +
//					"System.setProperty(\"freedom7.lib.Lib.executed\", \"true\");" +
//				"}" +
//			"}");
//		
//		new LibBuilder(_compiler, lib, sourceFolder("bin")).build(libJar);
//	}

//	private File generateWandY() throws IOException {
//		final File src = sourceFolder("src-wy");
//		final SourceFileWriter writer = new SourceFileWriter(src);
//		writeY(writer);
//		writeW(writer);
//		return src;
//	}

	private File generateY() throws IOException {
		File src = sourceFolder("src-y");
		writeY(new SourceFileWriter(src));
		return src;
	}
	
	private File sourceFolder(String sourceFolder) {
		return new File(tmpFolder(), sourceFolder);
	}

//
//	private File generateV() throws IOException {
//		File src = sourceFolder("src-v");
//		writeV(new SourceFileWriter(src));
//		return src;
//	}
//
	private void writeY(SourceFileWriter writer) throws IOException {
		writer.write("freedom7.y.Y", "@sneer.foundation.brickness.Brick public interface Y {}");
		writer.write("freedom7.y.impl.YImpl",
				"class YImpl implements freedom7.y.Y {\n" +
					"public YImpl() {\n" +
						"System.setProperty(\"freedom7.y.Y.installed\", \"true\");\n" +
					"}" +
				"}");
	}
//
//	private void writeV(SourceFileWriter writer) throws IOException {
//		writer.write("freedom7.v.V", "@sneer.foundation.brickness.Brick public interface V {}");
//		writer.write("freedom7.v.VTuple", "public class VTuple extends sneer.brickness.Tuple {}");
//		writer.write("freedom7.v.impl.VImpl",
//				"class VImpl implements freedom7.v.V {\n" +
//				"private freedom7.v.VTuple tuple;\n" +
//					"public VImpl() {\n" +
//						"System.setProperty(\"freedom7.v.V.installed\", \"true\");\n" +
//					"}" +
//				"}");
//	}

//	private File generateW() throws IOException {
//		final File src = sourceFolder("src-w");
//		writeW(new SourceFileWriter(src));
//		return src;
//	}

//	private void writeW(final SourceFileWriter writer) throws IOException {
//		writer.write("freedom7.w.W",
//				"@sneer.foundation.brickness.Brick\n" +
//				"public interface W {\n" +
//					"freedom7.y.Y y();\n" +
//				"}");
//		writer.write("freedom7.w.impl.WImpl",
//				"import sneer.brickness.*;\n" +
//				"import static sneer.foundation.commons.environments.Environments.my;\n" +
//				"class WImpl implements freedom7.w.W {\n" +
//					"private freedom7.y.Y _y = my(freedom7.y.Y.class);\n" +
//					"public WImpl() {\n" +
//						"if (_y == null) throw new IllegalStateException();\n" +
//						"System.setProperty(\"freedom7.w.W.installed\", \"true\");\n" +
//					"}" +
//					"public freedom7.y.Y y() {\n" +
//						"return _y;\n" +
//					"}\n" +
//				"}");
//	}
}