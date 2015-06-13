CREATE TABLE `reviews`.`SENTENCE_SENTIMENT` (
  `product_id` INT NOT NULL,
  `review_id` INT NOT NULL,
  `sentence_id` INT NOT NULL,
  `feature` VARCHAR(50) NOT NULL,
  `opinion` VARCHAR(50) NULL,
  `sentiment` INT NULL,
  PRIMARY KEY (`product_id`, `review_id`, `sentence_id`, `feature`));
