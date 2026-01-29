package com.example.demo.repository;

import com.example.demo.entity.Paste;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PasteRepository extends CrudRepository<Paste, UUID> {
}
