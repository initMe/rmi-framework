package org.wltea.analyzer.core;

public class SearchSegmenter implements ISegmenter {

	@Override
	public void analyze(AnalyzeContext context) {
		Lexeme newLexeme = new Lexeme(context.getBufferOffset(),
		context.getCursor(), 1, Lexeme.TYPE_CNWORD);
		context.addLexeme(newLexeme);
	}
	
	@Override
	public void reset() {
		
	}
}
