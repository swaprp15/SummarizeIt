CREATE TABLE `reviews`.`PRODUCT_REVIEWS` (
  `review_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `user` VARCHAR(100) NULL,
  `date_time` DATETIME NULL,
  `comment_text` TEXT NULL,
  `rating` INT NULL,
  `upvotes` INT NULL,
  `downvotes` VARCHAR(45) NULL,
  PRIMARY KEY (`review_id`, `product_id`));
