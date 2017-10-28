package org.vaslabs.example

trait PartitionKeyExtractor[A] {
  def extractKey(obj: A): String
}
