#--
# Copyright (c) 2007, Edwin Fine, Fine Computer Consultants, Inc.
# All rights reserved.
# 
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are
# met:
# 
#     * Redistributions of source code must retain the above copyright
#       notice, this list of conditions and the following disclaimer.
#     * Redistributions in binary form must reproduce the above copyright
#       notice, this list of conditions and the following disclaimer in the
#       documentation and/or other materials provided with the distribution.
#     * Neither the name of Diomidis Spinellis nor the names of its
#       contributors may be used to endorse or promote products derived from
#       this software without specific prior written permission.
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
# "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
# LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
# A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
# OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
# TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
# PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
# LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
# NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#++
# This program gets statistics based on the output of the ckjm program
# (http://www.spinellis.gr/sw/ckjm) and generates graphs in PNG format
# using gnuplot (V4 or later required). There is one graph per metric,
# which all get stored in the current directory. ckjm must create output
# using the "plain" (default) option.
#
# To run this program:
#
#   ruby mk_ckjm_graphs.rb ckjm-text-file
#
# ckjm plain output creates a text file with a line for each
# class. Each line consists of the class name, followed by 8 
# numeric metrics as described below. Values are space-separated.
# 
#   com.my.ClassName WMC DIT NOC CBO RFC LCOM Ca NPM
# 
# Example:
# 
#   com.vz.schedule.data.ScheduleData 15 1 0 0 20 69 3 15
#
# The following text is from Diomidis Spinellis' ckjm web page:
# 
# The metrics ckjm will calculate and display for each class are the
# following.
# 
# * <b>WMC - Weighted methods per class</b>. 
#   A class's weighted methods per class WMC metric is simply the sum
#   of the complexities of its methods. As a measure of complexity we
#   can use the cyclomatic complexity, or we can abritrarily assign a
#   complexity value of 1 to each method. The ckjm program assigns a
#   complexity value of 1 to each method, and therefore the value of
#   the WMC is equal to the number of methods in the class.
# * <b>DIT - Depth of Inheritance Tree</b>.
#   The depth of inheritance tree (DIT) metric provides for each class a
#   measure of the inheritance levels from the object hierarchy top. In
#   Java where all classes inherit Object the minimum value of DIT is 1.
# * <b>NOC - Number of Children</b>.
#   A class's number of children (NOC) metric simply measures the number
#   of immediate descendants of the class.
# * <b>CBO - Coupling between object classes</b>.
#   The coupling between object classes (CBO) metric represents the
#   number of classes coupled to a given class (efferent couplings,
#   Ce). This coupling can occur through method calls, field accesses,
#   inheritance, arguments, return types, and exceptions.
# * <b>RFC - Response for a Class</b>.
#   The metric called the response for a class (RFC) measures the
#   number of different methods that can be executed when an object of
#   that class receives a message (when a method is invoked for that
#   object). Ideally, we would want to find for each method of the class,
#   the methods that class will call, and repeat this for each called
#   method, calculating what is called the transitive closure of the
#   method's call graph. This process can however be both expensive
#   and quite inaccurate. In ckjm, we calculate a rough approximation
#   to the response set by simply inspecting method calls within the
#   class's method bodies. This simplification was also used in the 1994
#   Chidamber and Kemerer description of the metrics.
# * <b>LCOM - Lack of cohesion in methods</b>.
#   A class's lack of cohesion in methods (LCOM) metric counts the sets
#   of methods in a class that are not related through the sharing of
#   some of the class's fields. The original definition of this metric
#   (which is the one used in ckjm) considers all pairs of a class's
#   methods. In some of these pairs both methods access at least one
#   common field of the class, while in other pairs the two methods
#   to not share any common field accesses. The lack of cohesion in
#   methods is then calculated by subtracting from the number of method
#   pairs that don't share a field access the number of method pairs
#   that do. Note that subsequent definitions of this metric used as a
#   measurement basis the number of disjoint graph components of the
#   class's methods. Others modified the definition of connectedness
#   to include calls between the methods of the class. The program ckjm
#   follows the original (1994) definition by Chidamber and Kemerer.
# * <b>Ca - Afferent couplings</b>.
#   A class's afferent couplings is a measure of how many other classes
#   use the specific class. Ca is calculated using the same definition
#   as that used for calculating CBO (Ce).
# * <b>NPM - Number of Public Methods</b>.
#   The NPM metric simply counts all the methods in a class that are
#   declared as public. It can be used to measure the size of an API 
#   provided by a package.

require 'metrics_utils'

# This is the top-level application that ties together
# all the other elements to plot the ckjm data.
class App
  # Runs the application.
  # +argv+:: array containing <tt>[ckjm_file_name, project_desc]</tt>.
  def App.run(argv)
    App.usage unless argv.length == 2
    ckjm_file_name, project_desc = argv
    jm = MetricsUtils::JavaMetrics.new(ckjm_file_name)
    plotter = MetricsUtils::Plotter.instance
    grapher = MetricsUtils::CKJMMetricGrapher.new(ckjm_file_name, project_desc, plotter, jm)
    grapher.plot_metrics
  end

  private

  # Displays a usage message and exits.
  def App.usage
    puts "usage: ruby #{File.basename $0} ckjm-input-file project-desc"
    exit 1
  end
end

argv = ARGV.to_a
$debug_mode = argv.reject! {|elem| elem == "-d"}
App.run(argv)
