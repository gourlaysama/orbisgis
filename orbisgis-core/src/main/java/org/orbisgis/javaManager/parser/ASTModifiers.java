/* Generated By:JJTree: Do not edit this line. ASTModifiers.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.javaManager.parser;

public class ASTModifiers extends SimpleNode {
  public ASTModifiers(int id) {
    super(id);
  }

  public ASTModifiers(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=20855a56855f30713cc078fac50b3424 (do not edit this line) */