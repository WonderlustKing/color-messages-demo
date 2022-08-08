package com.chrisb.colors.prj.demo.color;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ColorRepository extends MongoRepository<ColorDomain, Long>, CustomColorRepository {

}
