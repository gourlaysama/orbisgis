/* Generated By:JJTree: Do not edit this line. ASTSQLLvalue.java */

package org.gdms.sql.parser;

public class ASTSQLLvalue extends SimpleNode {
  public ASTSQLLvalue(int id) {
    super(id);
  }

  public ASTSQLLvalue(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
