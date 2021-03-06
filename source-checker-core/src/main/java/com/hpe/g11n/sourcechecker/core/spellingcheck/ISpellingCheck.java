package com.hpe.g11n.sourcechecker.core.spellingcheck;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Foy Lian
 * Date: 2016-08-26
 * Time: 9:56
 */
public interface ISpellingCheck {
    /**
     * Verifies if the word to analyze is contained in dictionaries.
     * @param word The word to verify that it's spelling is known.
     * @return true if the word is in a dictionary.
     */
    boolean isCorrect(String word);

    /**
     * Produces a list of suggested word after looking for suggestions in various
     * dictionaries.
     * @param word The word for which we want to gather suggestions
     * @return the list of words suggested
     */
    List<String> getSuggestions(String word);
    
    /**
     * get the suggestion words 
     * @param word
     * @return
     */
	default String getSuggestionsLessThanThree(String word) {
		String result = "";
		List list = getSuggestions(word);
		for (int i = 0; i < list.size(); i++) {
			result = result + list.get(i) + ",";
			if (i == 2) {
				break;
			}
		}
		if (result.length() > 0) {
			result = result.substring(0, result.length() - 1);
		} else {
			result = "NA";
		}
		return result;
	}

    boolean isInDictionary(String word);
}
