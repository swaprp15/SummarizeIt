CREATE TABLE `reviews`.`REVIEW_SENTENCES` (
  `product_id` INT NOT NULL,
  `review_id` INT NOT NULL,
  `sentence_id` INT NOT NULL,
  `sentence_text` TEXT NULL,
  PRIMARY KEY (`product_id`, `review_id`, `sentence_id`));
