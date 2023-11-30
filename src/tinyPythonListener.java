// Generated from D:/JavaProject/CompilerProj/TinyToJVM/tinyPython.g4 by ANTLR 4.13.1

import org.antlr.v4.runtime.tree.ParseTreeListener;

import java.io.IOException;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link tinyPythonParser}.
 */
public interface tinyPythonListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(tinyPythonParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(tinyPythonParser.ProgramContext ctx) throws IOException;
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#file_input}.
	 * @param ctx the parse tree
	 */
	void enterFile_input(tinyPythonParser.File_inputContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#file_input}.
	 * @param ctx the parse tree
	 */
	void exitFile_input(tinyPythonParser.File_inputContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#defs}.
	 * @param ctx the parse tree
	 */
	void enterDefs(tinyPythonParser.DefsContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#defs}.
	 * @param ctx the parse tree
	 */
	void exitDefs(tinyPythonParser.DefsContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmt(tinyPythonParser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmt(tinyPythonParser.StmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#simple_stmt}.
	 * @param ctx the parse tree
	 */
	void enterSimple_stmt(tinyPythonParser.Simple_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#simple_stmt}.
	 * @param ctx the parse tree
	 */
	void exitSimple_stmt(tinyPythonParser.Simple_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#small_stmt}.
	 * @param ctx the parse tree
	 */
	void enterSmall_stmt(tinyPythonParser.Small_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#small_stmt}.
	 * @param ctx the parse tree
	 */
	void exitSmall_stmt(tinyPythonParser.Small_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#assignment_stmt}.
	 * @param ctx the parse tree
	 */
	void enterAssignment_stmt(tinyPythonParser.Assignment_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#assignment_stmt}.
	 * @param ctx the parse tree
	 */
	void exitAssignment_stmt(tinyPythonParser.Assignment_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#flow_stmt}.
	 * @param ctx the parse tree
	 */
	void enterFlow_stmt(tinyPythonParser.Flow_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#flow_stmt}.
	 * @param ctx the parse tree
	 */
	void exitFlow_stmt(tinyPythonParser.Flow_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#break_stmt}.
	 * @param ctx the parse tree
	 */
	void enterBreak_stmt(tinyPythonParser.Break_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#break_stmt}.
	 * @param ctx the parse tree
	 */
	void exitBreak_stmt(tinyPythonParser.Break_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#continue_stmt}.
	 * @param ctx the parse tree
	 */
	void enterContinue_stmt(tinyPythonParser.Continue_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#continue_stmt}.
	 * @param ctx the parse tree
	 */
	void exitContinue_stmt(tinyPythonParser.Continue_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterCompound_stmt(tinyPythonParser.Compound_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitCompound_stmt(tinyPythonParser.Compound_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#if_stmt}.
	 * @param ctx the parse tree
	 */
	void enterIf_stmt(tinyPythonParser.If_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#if_stmt}.
	 * @param ctx the parse tree
	 */
	void exitIf_stmt(tinyPythonParser.If_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#while_stmt}.
	 * @param ctx the parse tree
	 */
	void enterWhile_stmt(tinyPythonParser.While_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#while_stmt}.
	 * @param ctx the parse tree
	 */
	void exitWhile_stmt(tinyPythonParser.While_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#def_stmt}.
	 * @param ctx the parse tree
	 */
	void enterDef_stmt(tinyPythonParser.Def_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#def_stmt}.
	 * @param ctx the parse tree
	 */
	void exitDef_stmt(tinyPythonParser.Def_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#suite}.
	 * @param ctx the parse tree
	 */
	void enterSuite(tinyPythonParser.SuiteContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#suite}.
	 * @param ctx the parse tree
	 */
	void exitSuite(tinyPythonParser.SuiteContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#args}.
	 * @param ctx the parse tree
	 */
	void enterArgs(tinyPythonParser.ArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#args}.
	 * @param ctx the parse tree
	 */
	void exitArgs(tinyPythonParser.ArgsContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#return_stmt}.
	 * @param ctx the parse tree
	 */
	void enterReturn_stmt(tinyPythonParser.Return_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#return_stmt}.
	 * @param ctx the parse tree
	 */
	void exitReturn_stmt(tinyPythonParser.Return_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#test}.
	 * @param ctx the parse tree
	 */
	void enterTest(tinyPythonParser.TestContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#test}.
	 * @param ctx the parse tree
	 */
	void exitTest(tinyPythonParser.TestContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#print_stmt}.
	 * @param ctx the parse tree
	 */
	void enterPrint_stmt(tinyPythonParser.Print_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#print_stmt}.
	 * @param ctx the parse tree
	 */
	void exitPrint_stmt(tinyPythonParser.Print_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#print_arg}.
	 * @param ctx the parse tree
	 */
	void enterPrint_arg(tinyPythonParser.Print_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#print_arg}.
	 * @param ctx the parse tree
	 */
	void exitPrint_arg(tinyPythonParser.Print_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void enterComp_op(tinyPythonParser.Comp_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void exitComp_op(tinyPythonParser.Comp_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(tinyPythonParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(tinyPythonParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tinyPythonParser#opt_paren}.
	 * @param ctx the parse tree
	 */
	void enterOpt_paren(tinyPythonParser.Opt_parenContext ctx);
	/**
	 * Exit a parse tree produced by {@link tinyPythonParser#opt_paren}.
	 * @param ctx the parse tree
	 */
	void exitOpt_paren(tinyPythonParser.Opt_parenContext ctx);
}