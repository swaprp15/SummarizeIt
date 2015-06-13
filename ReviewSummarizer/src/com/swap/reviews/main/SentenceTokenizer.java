package com.swap.reviews.main;

import java.util.List;

import com.swap.reviews.domain.Sentence;

public interface SentenceTokenizer {

  List<Sentence> toSentences(final String text);
}
