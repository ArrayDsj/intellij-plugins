package com.jetbrains.dart.analysisServer;

import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import com.jetbrains.lang.dart.analyzer.DartAnalysisServerService;
import com.jetbrains.lang.dart.ide.generation.BaseDartGenerateHandler;
import com.jetbrains.lang.dart.ide.generation.DartGenerateEqualsAndHashcodeHandler;
import com.jetbrains.lang.dart.util.DartTestUtils;
import org.jetbrains.annotations.NotNull;

public class DartCodeGenerationTest extends CodeInsightFixtureTestCase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    DartTestUtils.configureDartSdk(myModule, myFixture.getTestRootDisposable(), true);
    DartAnalysisServerService.getInstance(getProject()).serverReadyForRequest(getProject());
  }

  protected void doEqualsAndHashcodeTest(@NotNull final String before, @NotNull final String after) {
    doTest(before, after, new DartGenerateEqualsAndHashcodeHandler());
  }

  protected void doTest(@NotNull final String before, @NotNull final String after, @NotNull final BaseDartGenerateHandler anAction) {
    myFixture.configureByText("foo.dart", before);
    anAction.invoke(getProject(), getEditor(), getFile());
    myFixture.checkResult(after);
  }

  public void testEqualsAndHashcodeNoSuper() throws Exception {
    doEqualsAndHashcodeTest("class Interface {\n" +
                            "  bool operator ==(Object other) => super == other;\n" +
                            "  int get hashCode => super.hashCode;\n" +
                            "}\n" +
                            "class Bar {}\n" +
                            "class Baz implements Interface {}\n" +
                            "class Foo extends Bar with Baz implements Interface {<caret>}\n",

                            "class Interface {\n" +
                            "  bool operator ==(Object other) => super == other;\n" +
                            "  int get hashCode => super.hashCode;\n" +
                            "}\n" +
                            "class Bar {}\n" +
                            "class Baz implements Interface {}\n" +
                            "class Foo extends Bar with Baz implements Interface {\n" +
                            "  @override\n" +
                            "  bool operator ==(Object other) =>\n" +
                            "      identical(this, other) ||\n" +
                            "          other is Foo &&\n" +
                            "              runtimeType == other.runtimeType;\n" +
                            "\n" +
                            "  @override\n" +
                            "  int get hashCode => 0;\n" +
                            "}\n");
  }

  public void testEqualsAndHashcodeWithSuper() throws Exception {
    doEqualsAndHashcodeTest("class Bar extends Baz {}\n" +
                            "class Baz {\n" +
                            "  bool operator ==(Object other) => super == other;\n" +
                            "  int get hashCode => super.hashCode;\n" +
                            "}\n" +
                            "class Foo extends Bar {<caret>}",

                            "class Bar extends Baz {}\n" +
                            "class Baz {\n" +
                            "  bool operator ==(Object other) => super == other;\n" +
                            "  int get hashCode => super.hashCode;\n" +
                            "}\n" +
                            "class Foo extends Bar {\n" +
                            "  @override\n" +
                            "  bool operator ==(Object other) =>\n" +
                            "      identical(this, other) ||\n" +
                            "          super == other &&\n" +
                            "              other is Foo &&\n" +
                            "              runtimeType == other.runtimeType;\n" +
                            "\n" +
                            "  @override\n" +
                            "  int get hashCode =>\n" +
                            "      super.hashCode;\n" +
                            "}");
  }

  public void testEqualsAndHashcodeWithFieldsNoSuper() throws Exception {
    doEqualsAndHashcodeTest("class Interface {\n" +
                            "  bool operator ==(Object other) => super == other;\n" +
                            "  int get hashCode => super.hashCode;\n" +
                            "}\n" +
                            "class Foo extends Object implements Interface {\n" +
                            "  Error e;\n" +
                            "  bool b;\n" +
                            "<caret>}",

                            "class Interface {\n" +
                            "  bool operator ==(Object other) => super == other;\n" +
                            "  int get hashCode => super.hashCode;\n" +
                            "}\n" +
                            "class Foo extends Object implements Interface {\n" +
                            "  Error e;\n" +
                            "  bool b;\n" +
                            "\n" +
                            "  @override\n" +
                            "  bool operator ==(Object other) =>\n" +
                            "      identical(this, other) ||\n" +
                            "          other is Foo &&\n" +
                            "              runtimeType == other.runtimeType &&\n" +
                            "              e == other.e &&\n" +
                            "              b == other.b;\n" +
                            "\n" +
                            "  @override\n" +
                            "  int get hashCode =>\n" +
                            "      e.hashCode ^\n" +
                            "      b.hashCode;\n" +
                            "  \n" +
                            "}");
  }

  public void testEqualsAndHashcodeWithFieldsAndSuper() throws Exception {
    doEqualsAndHashcodeTest("class Bar extends Baz {var qwe;}\n" +
                            "class Baz {\n" +
                            "  bool operator ==(Object other) => super == other;\n" +
                            "  int get hashCode => super.hashCode;\n" +
                            "}\n" +
                            "class Foo extends Bar {\n" +
                            "  Error e;\n" +
                            "  bool b;<caret>\n" +
                            "}",

                            "class Bar extends Baz {var qwe;}\n" +
                            "class Baz {\n" +
                            "  bool operator ==(Object other) => super == other;\n" +
                            "  int get hashCode => super.hashCode;\n" +
                            "}\n" +
                            "class Foo extends Bar {\n" +
                            "  Error e;\n" +
                            "  bool b;\n" +
                            "\n" +
                            "  @override\n" +
                            "  bool operator ==(Object other) =>\n" +
                            "      identical(this, other) ||\n" +
                            "          super == other &&\n" +
                            "              other is Foo &&\n" +
                            "              runtimeType == other.runtimeType &&\n" +
                            "              e == other.e &&\n" +
                            "              b == other.b;\n" +
                            "\n" +
                            "  @override\n" +
                            "  int get hashCode =>\n" +
                            "      super.hashCode ^\n" +
                            "      e.hashCode ^\n" +
                            "      b.hashCode;\n" +
                            "  \n" +
                            "}");
  }
}
