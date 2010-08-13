/*
 Copyright 2009 David Hall, Daniel Ramage

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/
package scalanlp.stage.source;

import java.io.File;

import scalanlp.ra.RA;

import scalanlp.stage.{Parcel,Batch,History};

/**
 * A TSV file acts as a source of Array[String] is a simple tab-delimited
 * file with no escaping.  The incoming strings are simply split on tab and
 * outgoing strings have their tabs replaced by a single space character.
 * 
 * @author dramage
 */
case class TSVFile(path : String) extends File(path) { self =>
  import RA.global.pipes._;

  def iterator : Iterator[Seq[String]] =
    TSVFile.this.getLines.map(_.split("\t",-1));

  def rows : Iterable[Seq[String]] = {
    new Iterable[Seq[String]] {
      override def iterator = self.iterator;
    };
  }

  def asParcel : Parcel[Batch[Seq[String]]] =
    Parcel(History.Origin(toString), Batch.fromIterator(() => iterator));

  override def toString =
    "TSVFile(\""+path+"\")";
}

/**
 * Companion object for TSVFiles that includes an implicit conversion
 * to a parcel and a format method that correctly escapes and joins
 * a sequence of strings.
 *
 * @author dramage
 */
object TSVFile {

  /** Calls file.asParcel. */
  implicit def TSVFileAsParcel(file : TSVFile) = file.asParcel;

  /** Formats the given sequence of strings as well-formed line of TSV. */
  def format(seq : Seq[String]) : String =
    seq.map(_.replaceAll("\\s"," ")).mkString("\t");
}
