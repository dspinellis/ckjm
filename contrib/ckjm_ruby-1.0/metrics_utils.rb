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
# This module contains utilities to plot Chidamber-Kemerer metrics.
module MetricsUtils

  require 'singleton'
  
  # This class exists to provide access to the underlying plotting mechanism.
  # It configures the plotter program and accepts plot data to send to the program.
  # At the moment, it uses gnuplot as the plotter program. It seems unlikely to
  # change in the foreseeable future unless I make a full plotter abstraction,
  # which given the time I have to do this, is about as likely as me winning
  # the jackpot.
  class Plotter
    include Singleton

    # Initializes the plotter instance
    def initialize
      @plot_cmd = "gnuplot -"
      @plot_cmd << " > /dev/null 2>&1" unless $debug_mode
    end

    # Opens the plotter for writing. If a block is provided,
    # calls that block and closes the plotter when the block
    # returns. Always returns +nil+.
    def open(&blk) # :yields:
      @plot_pipe = IO.popen(@plot_cmd, "w")

      if blk
        begin
          blk.call
        ensure
          self.close
        end
      end
      nil
    end

    # Closes the plotter if it's open.
    # Returns nil.
    def close
      @plot_pipe && @plot_pipe.close
    ensure
      @plot_pipe = nil
    end

    # Prints plot data to the plotter.
    def print_to_graph(plot_data)
      @plot_pipe.puts plot_data
      puts(plot_data) if $debug_mode
    end

    # Sends an "end of data" signal to the plotter
    # to signify the closing of a plot.
    def end_of_data
      print_to_graph "e" # gnuplot-specific
    end

  end # class Plotter

  # This class parses the text output of the ckjm program
  # and stores the data points.
  class JavaMetrics
    # :stopdoc:
    # Metrics are expected in this order in the input record
    METRICS = [
    [ :wmc,  'Weighted methods per class' ],
    [ :dit,  'Depth of Inheritance Tree' ],
    [ :noc,  'Number of Children' ],
    [ :cbo,  'Coupling between object classes' ],
    [ :rfc,  'Response for a Class' ],
    [ :lcom, 'Lack of cohesion in methods' ],
    [ :ca,   'Afferent couplings' ],
    [ :npm,  'Number of Public Methods' ]
    ].freeze # :nodoc:

    # Maps metric symbols to long descriptions
    METRIC_DESC = Hash[*METRICS.flatten].freeze
    # :startdoc:

    # Initializes this instance with the name of the
    # text file containing the output of the ckjm
    # program.
    def initialize(ckjm_file_name)
      @ckjm_file_name = ckjm_file_name
      @distribution = {}
    end

    # Computes the distribution of the 8 ckjm metrics
    # by counting how many classes fall into a bucket
    # with a specific metric value. Buckets are just
    # individual metric values, (e.g. 1, 2, 3 for DIT).
    # Returns the distribution as a hash of hashes:
    #   { metric_sym => { metric_value => count } }
    # Example:
    #   { :wmc => { 7 => 134 } }
    def metrics
      @distribution.clear
      File.foreach(@ckjm_file_name) do |line|
        line.chomp!
        if line =~ /^\S+(\s+\d+){8}\s*$/
          class_name, *metrics_vals = line.split(/\s+/)
          METRICS.each_index do |i|
            value = metrics_vals[i].to_i
            (@distribution[METRICS[i][0]] ||= Hash.new(0))[value] += 1
          end
        end
      end
      @distribution
    end

    # Returns string description corresponding
    # to +sym+; raises +ArgumentError+ if +sym+ unknown.
    # Valid values for +sym+ are :wmc, :dit, :noc,
    # :cbo, :rfc, :lcom, :ca, and :npm.
    def metric_desc(sym)
      METRIC_DESC[sym] or raise ArgumentError, "Unknown symbol :#{sym}"
    end
  end

  # This class graphs the Chidamber-Kemerer metrics to PNG
  # files, as currently configured. It needs gnuplot v4.0 or later.
  class CKJMMetricGrapher
    FILE_FORMAT = "png"
    FORMAT_OPTIONS = "small"

    # Initializes the instance.
    # +ckjm_file_name+:: Input file
    # +project_desc+:: Short project description to go on graphs
    # +plotter+:: A Plotter-like object that responds to open, close,
    #             print_to_graph(str), and end_of_data
    # +jm+:: An instance of the JavaMetrics object
    def initialize(ckjm_file_name, project_desc, plotter, jm)
      @ckjm_file_name, @project_desc, @plotter, @jm = ckjm_file_name, project_desc, plotter, jm
    end

    # Plots all metrics
    def plot_metrics
      @jm.metrics.each do |metric_sym, vals|
        @plotter.open do
          plot_metric metric_sym, vals
        end
      end
    end

    private

    # Returns file name for graph
    def fmt_graph_filename(metric_sym)
      "#{@project_desc.gsub(/\s+/, '_')}_#{metric_sym.to_s}_metric.png"
    end

    # Returns graph title
    def fmt_graph_title(metric_sym)
      "#{@jm.metric_desc(metric_sym)} (#{metric_sym.to_s.upcase})"
    end

    # Plots a single metric whose symbol is *sym* (e.g. :dit)
    # and which consists of the hash *vals*. Each hash key/value
    # pair is <tt>{ metric_value => count }</tt>.
    def plot_metric(metric_sym, vals)
      @plotter.print_to_graph %{
        set terminal #{FILE_FORMAT} #{FORMAT_OPTIONS}
        set output '#{fmt_graph_filename(metric_sym)}'
        set title '#{fmt_graph_title(metric_sym)}'
        set xrange [-0.5:*]
        set xlabel '#{metric_sym.to_s.upcase}'
        set ylabel 'Number of classes'
        set timestamp 'Generated for #@project_desc on %Y/%m/%d' top
        set boxwidth 0.8 absolute
        set style fill solid 0.25 border
        show timestamp
        plot '-' using 1:2 notitle with boxes
      }

      vals.sort.each do |point|
        @plotter.print_to_graph point.join(" ")
      end

      @plotter.end_of_data
      @plotter.print_to_graph "set output" # close file, end of plot
    end

  end
end
