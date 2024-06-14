package com.english.eva.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.english.eva.entity.Example;

public interface ExampleRepository extends JpaRepository<Example, Long> {
}
