class Neatvnc < Formula
  desc "Development framework for multimedia applications"
  homepage "https://gstreamer.freedesktop.org/"
  url "https://github.com/any1/neatvnc/archive/refs/tags/v0.8.0.tar.gz"
  sha256 "6c0feff5d8de20d1f47938936fd2c0e99dc56c28033e2149863cf70ce6cfcc5c"
  license "LGPL-2.1-or-later"

  depends_on "meson" => :build
  depends_on "ninja" => :build
  depends_on "cmake" => :build
  depends_on "libdrm" => :build
  depends_on "pixman" => :build
  depends_on "aml" => :build

  def install
    system "meson", *std_meson_args, ".", "output"
    system "ninja", "-C", "output"
    system "ninja", "-C", "output", "install"
  end

end
