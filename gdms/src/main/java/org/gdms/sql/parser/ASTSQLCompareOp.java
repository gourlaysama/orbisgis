/* Generated By:JJTree: Do not edit this line. ASTSQLCompareOp.java */

package org.gdms.sql.parser;

public class ASTSQLCompareOp extends SimpleNode {
  public ASTSQLCompareOp(int id) {
    super(id);
  }

  public ASTSQLCompareOp(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
