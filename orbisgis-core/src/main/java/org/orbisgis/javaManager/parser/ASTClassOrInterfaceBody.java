/* Generated By:JJTree: Do not edit this line. ASTClassOrInterfaceBody.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.javaManager.parser;

public class ASTClassOrInterfaceBody extends SimpleNode {
  public ASTClassOrInterfaceBody(int id) {
    super(id);
  }

  public ASTClassOrInterfaceBody(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=dcfb50078b79f414bc2cab39410f9a39 (do not edit this line) */