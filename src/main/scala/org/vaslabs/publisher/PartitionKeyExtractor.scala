package org.vaslabs.publisher

trait PartitionKeyExtractor[A] {
  def extractKey(obj: A): String
}
