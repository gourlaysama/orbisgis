/* Generated By:JJTree: Do not edit this line. ASTAssertStatement.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.javaManager.parser;

public class ASTAssertStatement extends SimpleNode {
  public ASTAssertStatement(int id) {
    super(id);
  }

  public ASTAssertStatement(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=99a7a0a836609b7d2771aacfd6d054ba (do not edit this line) */