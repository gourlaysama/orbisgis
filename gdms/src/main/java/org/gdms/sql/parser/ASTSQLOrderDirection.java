/* Generated By:JJTree: Do not edit this line. ASTSQLOrderDirection.java */

package org.gdms.sql.parser;

public class ASTSQLOrderDirection extends SimpleNode {
  public ASTSQLOrderDirection(int id) {
    super(id);
  }

  public ASTSQLOrderDirection(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
